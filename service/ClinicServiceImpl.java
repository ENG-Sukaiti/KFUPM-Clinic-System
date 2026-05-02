package kfupm.clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import kfupm.clinic.api.Result;
import kfupm.clinic.ds.*;
import kfupm.clinic.model.*;

/**
 * Students implement the system logic here.
 *
 * Rules:
 * - Use the provided custom data structures.
 * - Do NOT use Java built-in maps/trees/priority queues for storage.
 */

public class ClinicServiceImpl implements ClinicService {

    private final HashTable<String, Patient> patientsById = new HashTable<>();
    private final HashTable<String, Appointment> apptsById = new HashTable<>();
    private final AVLTree<AppointmentKey, Appointment> apptsByTime = new AVLTree<>();
    private final LinkedQueue<Patient> walkIns = new LinkedQueue<>();
    private final MaxHeap<UrgentPatient> urgentHeap = new MaxHeap<>((a, b) -> {
        if (a.severity() != b.severity()) return Integer.compare(a.severity(), b.severity());
        return Long.compare(b.arrivalEpochMillis(), a.arrivalEpochMillis());
    });
    private final LinkedStack<Action> undo = new LinkedStack<>();
    private final SinglyLinkedList<VisitLogEntry> log = new SinglyLinkedList<>();
    private final StringMatcher naive = new NaiveMatcher();
    private final StringMatcher kmp = new KMPMatcher();
    private int nextApptId = 1;

    @Override
    public Result<Void> addPatient(String id, String name, String phone) {
        if (patientsById.get(id) != null) return Result.fail("Exists");
        patientsById.put(id, new Patient(id, name, phone));
        return Result.ok(null, "Added");
    }

    @Override
    public Result<Patient> findPatient(String id) {
        Patient p = patientsById.get(id);
        return p == null ? Result.fail("No") : Result.ok(p, "Yes");
    }

    @Override
    public Result<Void> deletePatient(String id) {
        if (patientsById.remove(id) == null) return Result.fail("No");
        return Result.ok(null, "Deleted");
    }

    @Override
    public Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor) {
        Patient p = patientsById.get(patientId);
        if (p == null) return Result.fail("No Patient");
        String id = "A" + (nextApptId++);
        Appointment appt = new Appointment(id, patientId, p.name(), p.phone(), date, time, doctor);
        apptsById.put(id, appt);
        apptsByTime.put(new AppointmentKey(date, time, id), appt);
        return Result.ok(id, "Added");
    }

    @Override
    public Result<Void> cancelAppointment(String appointmentId) {
        Appointment a = apptsById.remove(appointmentId);
        if (a == null) return Result.fail("No");
        apptsByTime.remove(new AppointmentKey(a.date(), a.time(), appointmentId));
        return Result.ok(null, "Cancelled");
    }

    @Override
    public Result<Appointment> findAppointment(String id) {
        Appointment a = apptsById.get(id);
        return a == null ? Result.fail("No") : Result.ok(a, "Yes");
    }

    @Override
    public List<Appointment> viewDay(LocalDate date) {
        List<Appointment> list = new ArrayList<>();
        apptsByTime.inOrder((k, v) -> { if (k.date().equals(date)) list.add(v); });
        return list;
    }

    @Override
    public List<Appointment> viewRange(LocalDate date, LocalTime s, LocalTime e) {
        List<Appointment> list = new ArrayList<>();
        apptsByTime.inOrder((k, v) -> {
            if (k.date().equals(date) && !k.time().isBefore(s) && !k.time().isAfter(e)) list.add(v);
        });
        return list;
    }

    @Override
    public Result<Void> addWalkIn(String id) {
        Patient p = patientsById.get(id);
        if (p == null) return Result.fail("No");
        walkIns.enqueue(p);
        return Result.ok(null, "Added");
    }

    @Override
    public List<Patient> viewWalkIns() { return walkIns.toList(); }

    @Override
    public Result<Void> addUrgent(String id, int s) {
        Patient p = patientsById.get(id);
        if (p == null) return Result.fail("No");
        urgentHeap.push(new UrgentPatient(p, s, System.currentTimeMillis()));
        return Result.ok(null, "Added");
    }

    @Override
    public Result<UrgentPatient> peekUrgent() {
        UrgentPatient u = urgentHeap.peek();
        return u == null ? Result.fail("No") : Result.ok(u, "Yes");
    }

    @Override
    public List<UrgentPatient> viewUrgentsSnapshot() { return urgentHeap.toListSnapshot(); }

    @Override
    public Result<VisitLogEntry> serveNext(String d, String n) {
        String pId;
        String type;
        if (!urgentHeap.isEmpty()) {
            pId = urgentHeap.pop().patient().id();
            type = "URGENT";
        } else if (!walkIns.isEmpty()) {
            pId = walkIns.dequeue().id();
            type = "WALKIN";
        } else {
            var entry = apptsByTime.minEntry();
            if (entry == null) return Result.fail("Empty");
            pId = entry.value().patientId();
            type = "APPOINTMENT";
            cancelAppointment(entry.value().appointmentId());
        }
        Patient p = patientsById.get(pId);
        VisitLogEntry logE = new VisitLogEntry(System.currentTimeMillis(), pId, p.name(), type, d, n);
        log.addLast(logE);
        return Result.ok(logE, "Served");
    }

    @Override
    public List<VisitLogEntry> printLog() { return log.toList(); }

    @Override
    public List<VisitLogEntry> searchLogNaive(String pat) {
        List<VisitLogEntry> res = new ArrayList<>();
        for (VisitLogEntry e : log.toList()) if (naive.contains(e.notes(), pat)) res.add(e);
        return res;
    }

    @Override
    public List<VisitLogEntry> searchLogKmp(String pat) {
        List<VisitLogEntry> res = new ArrayList<>();
        for (VisitLogEntry e : log.toList()) if (kmp.contains(e.notes(), pat)) res.add(e);
        return res;
    }

    @Override
    public Result<Action> undo() { return Result.fail("No"); }
}