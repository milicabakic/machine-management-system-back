package com.example.webdomaci3.service;

import com.example.webdomaci3.model.Machine;
import com.example.webdomaci3.model.User;
import com.example.webdomaci3.repository.MachineRepository;
import com.example.webdomaci3.requests.FilterMachineRequest;
import com.example.webdomaci3.utils.MachineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class MachineService {

    private MachineRepository machineRepository;
    private TaskScheduler taskScheduler;
    private ErrorMessageService errorMessageService;


    @Autowired
    public MachineService(MachineRepository machineRepository, TaskScheduler taskScheduler, ErrorMessageService errorMessageService){
        this.machineRepository = machineRepository;
        this.taskScheduler = taskScheduler;
        this.errorMessageService = errorMessageService;
    }

    public Machine addMachine(String machineName, User user){
        Machine machine = new Machine();
        machine.setCreatedBy(user);
        machine.setActive(true);
        machine.setDateFrom(new Date());
        machine.setStatus(MachineUtil.STATUS_STOPPED);
        if(machineName != ""){
            machine.setName(machineName);
        }
        return machineRepository.save(machine);
    }

    @Transactional
    public ResponseEntity<?> destroyMachine(Long id, User creator) {
        if(machineRepository.existsByIdAndStatusAndCreatedBy(id, MachineUtil.STATUS_STOPPED, creator)){
            machineRepository.destroy(id);
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(400).build();
    }

    public Page<Machine> findAll(User user, Integer page, Integer size){
        return machineRepository.findByCreatedByAndActive(user,true,((Pageable) PageRequest.of(page, size)));
    }

    public List<Machine> search(User user, FilterMachineRequest filterMachineRequest, Integer page, Integer size) {
        Machine machine = new Machine();
       // machine.setStatus(filterMachineRequest.getStatus());
        machine.setActive(true);
        machine.setCreatedBy(user);
        machine.setName(filterMachineRequest.getName());

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Machine> example = Example.of(machine, exampleMatcher);

        if(filterMachineRequest.getDateFrom() != null && filterMachineRequest.getDateTo() != null)
            return machineRepository.searchMachines(user, filterMachineRequest.getName(), filterMachineRequest.getStatus(),
                    filterMachineRequest.getDateFrom(), filterMachineRequest.getDateTo());
          //  return machineRepository.findByDateFromBetween(filterMachineRequest.getDateFrom(), filterMachineRequest.getDateTo(),((Pageable) PageRequest.of(page, size)));

       // return machineRepository.findAll(example, ((Pageable) PageRequest.of(page, size)));
        return machineRepository.searchMachines(user, filterMachineRequest.getName(), filterMachineRequest.getStatus());
    }

    @Async
    public void startMachine(Long id, String username, boolean scheduled) {
        System.out.println("Starting machine " + id + "...");
        Machine machine = machineRepository.getById(id);
        if(!machine.getCreatedBy().getUsername().equals(username)){
            System.out.println("Forbidden access.");
            return;
        }
        if(machine.isInUse()){
            System.out.println("Could not proceed this action. Another action is launched.");
            if(scheduled){
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_START, MachineUtil.ERROR_IN_USE);
            }
            return;
        }
        if(!machine.getStatus().equals(MachineUtil.STATUS_STOPPED)){
            System.out.println("Machine has already been runned.");
            if(scheduled) {
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_START, MachineUtil.ERROR_STATUS);
            }
            return;
        }
        machine.setInUse(true);
        machineRepository.save(machine);
        try {
            Thread.sleep(10000 + (long)Math.random()*5000);
            machine.setStatus(MachineUtil.STATUS_RUNNING);
            machine.setInUse(false);
            machineRepository.save(machine);
            System.out.println("Machine " + id + " is STARTED");
        } catch (InterruptedException e) {
            //
        }
    }

    @Async
    public void stopMachine(Long id, String username, boolean scheduled){
        System.out.println("Stopping machine " + id + "...");
        Machine machine = machineRepository.getById(id);
        if(!machine.getCreatedBy().getUsername().equals(username)){
            System.out.println("Forbidden access.");
            return;
        }
        if(machine.isInUse()){
            System.out.println("Could not proceed this action. Another action is launched.");
            if(scheduled) {
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_STOP, MachineUtil.ERROR_IN_USE);
            }
            return;
        }
        if(!machine.getStatus().equals(MachineUtil.STATUS_RUNNING)){
            System.out.println("Machine has already been stopped.");
            if(scheduled) {
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_STOP, MachineUtil.ERROR_STATUS);
            }
            return;
        }
        machine.setInUse(true);
        machine = machineRepository.saveAndFlush(machine);
        try {
            Thread.sleep(10000 + (long)Math.random()*5000);
            machine.setStatus(MachineUtil.STATUS_STOPPED);
            machine.setInUse(false);
            machineRepository.save(machine);
            System.out.println("Machine " + id + " is STOPPED");
        } catch (InterruptedException e) {
            //
        }
    /*    catch (ObjectOptimisticLockingFailureException e){
            System.out.println("Another action is launched.");
            e.printStackTrace();
        }*/
    }

    @Async
   // @Transactional
    public void restartMachine(Long id, String username, boolean scheduled){
        System.out.println("Restarting machine " + id + "...");
        Machine machine = machineRepository.getById(id);
        if(!machine.getCreatedBy().getUsername().equals(username)){
            System.out.println("Forbidden access.");
            return;
        }
        if(machine.isInUse()){
            System.out.println("Could not proceed this action. Another action is launched.");
            if(scheduled) {
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_RESTART, MachineUtil.ERROR_IN_USE);
            }
            return;
        }
        if(!machine.getStatus().equals(MachineUtil.STATUS_RUNNING)){
            System.out.println("Machine has already been stopped.");
            if(scheduled) {
                errorMessageService.saveError(machine, new Date(), MachineUtil.ACTION_RESTART, MachineUtil.ERROR_STATUS);
            }
            return;
        }
        machine.setInUse(true);
        machine = machineRepository.saveAndFlush(machine);
        try {
            Thread.sleep(5000 + (long)Math.random()*5000);
            machine.setStatus(MachineUtil.STATUS_STOPPED);
            machine = machineRepository.saveAndFlush(machine);
            System.out.println("Machine " + id + " is STOPPED");
            Thread.sleep(5000 + (long)Math.random()*5000);
            machine.setStatus(MachineUtil.STATUS_RUNNING);
            machine.setInUse(false);
            machineRepository.saveAndFlush(machine);
            System.out.println("Machine " + id + " is RESTARTED");
        } catch (InterruptedException e) {
            //
        }
      /*  catch (ObjectOptimisticLockingFailureException e){
            System.out.println("Another action is launched.");
            e.printStackTrace();
        }

       */
    }

    @Async
   // @Transactional
    public void scheduleAction(Long id, String username, String action, Date date) {
        System.out.println("Zakazivanje akcije");
        System.out.println(date.toString());
        date.setHours(date.getHours() - 1);
        System.out.println(date.toString());
        this.taskScheduler.schedule(() -> {
            try {
                if (action.equals(MachineUtil.ACTION_START)) {
                    this.startMachine(id, username, true);
                } else if (action.equals(MachineUtil.ACTION_RESTART)) {
                    this.restartMachine(id, username, true);
                } else {
                    this.stopMachine(id, username, true);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }, date);
    }

}
