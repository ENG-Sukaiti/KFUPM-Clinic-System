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
        if (patientsById.get(id) != null) {
            return Result.fail("Patient already exists");
        }
        patientsById.put(id, new Patient(id, name, phone));
        return Result.ok(null, "Patient added successfully");
    }

    @Override
    public Result<Patient> findPatient(String id) {
        Patient p = patientsById.get(id);
        if (p == null) return Result.fail("Patient not found");
        return Result.ok(p, "Patient found");
    }

    @Override
    public Result<Void> deletePatient(String id) {
        Patient p = patientsById.remove(id);
        if (p == null) return Result.fail("Patient not found");
        return Result.ok(null, "Patient deleted");
    }

    @Override
    public Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor) {
        if (patientsById.get(patientId) == null) {
            return Result.fail("Patient not found");
        }
        String id = newAppointmentId();
        Appointment appt = new Appointment(id, patientId, date, time, doctor);
        AppointmentKey key = new AppointmentKey(date, time);
        apptsById.put(id, appt);
        apptsByTime.put(key, appt);
        return Result.ok(id, "Appointment added");
    }

    @Override
    public Result<Void> cancelAppointment(String appointmentId) {
        Appointment appt = apptsById.remove(appointmentId);
        if (appt == null) {
            return Result.fail("Appointment not found");
        }
        apptsByTime.remove(new AppointmentKey(appt.date(), appt.time()));
        return Result.ok(null, "Appointment cancelled");
    }

    @Override
    public Result<Appointment> findAppointment(String appointmentId) {
        Appointment appt = apptsById.get(appointmentId);
        if (appt == null) return Result.fail("Appointment not found");
        return Result.ok(appt, "Appointment found");
    }

    @Override
    public List<Appointment> viewDay(LocalDate date) {
        List<Appointment> list = new ArrayList<>();
        apptsByTime.inOrder((k, v) -> {
            if (k.date().equals(date)) {
                list.add(v);
            }
        });
        return list;
    }

    @Override
    public List<Appointment> viewRange(LocalDate date, LocalTime start, LocalTime end) {
        List<Appointment> list = new ArrayList<>();
        apptsByTime.inOrder((k, v) -> {
            if (k.date().equals(date) && !k.time().isBefore(start) && !k.time().isAfter(end)) {
                list.add(v);
            }
        });
        return list;
    }

    @Override
    public Result<Void> addWalkIn(String patientId) {
        Patient p = patientsById.get(patientId);
        if (p == null) return Result.fail("Patient not found");
        walkIns.enqueue(p);
        return Result.ok(null, "Walk-in added");
    }

    @Override
    public List<Patient> viewWalkIns() {
        return walkIns.toList();
    }

    @Override
    public Result<Void> addUrgent(String patientId, int severity) {
        Patient p = patientsById.get(patientId);
        if (p == null) return Result.fail("Patient not found");
        urgentHeap.push(new UrgentPatient(patientId, severity, System.currentTimeMillis()));
        return Result.ok(null, "Urgent patient added");
    }

    @Override
    public Result<UrgentPatient> peekUrgent() {
        UrgentPatient up = urgentHeap.peek();
        if (up == null) return Result.fail("No urgent patients");
        return Result.ok(up, "Most urgent patient");
    }

    @Override
    public List<UrgentPatient> viewUrgentsSnapshot() {
        return urgentHeap.toListSnapshot();
    }

    @Override
    public Result<VisitLogEntry> serveNext(String doctor, String note) {
        if (!urgentHeap.isEmpty()) {
            UrgentPatient up = urgentHeap.pop();
            VisitLogEntry entry = new VisitLogEntry(up.patientId(), doctor, note);
            log.addLast(entry);
            return Result.ok(entry, "Served urgent patient");
        }
        if (!walkIns.isEmpty()) {
            Patient p = walkIns.dequeue();
            VisitLogEntry entry = new VisitLogEntry(p.id(), doctor, note);
            log.addLast(entry);
            return Result.ok(entry, "Served walk-in patient");
        }
        AVLTree.Entry<AppointmentKey, Appointment> minEntry = apptsByTime.minEntry();
        if (minEntry != null) {
            Appointment appt = minEntry.value();
            apptsByTime.remove(minEntry.key());
            apptsById.remove(appt.id());
            VisitLogEntry entry = new VisitLogEntry(appt.patientId(), doctor, note);
            log.addLast(entry);
            return Result.ok(entry, "Served appointment patient");
        }
        return Result.fail("No patients to serve");
    }

    @Override
    public List<VisitLogEntry> printLog() {
        return log.toList();
    }

    @Override
    public List<VisitLogEntry> searchLogNaive(String pattern) {
        List<VisitLogEntry> results = new ArrayList<>();
        List<VisitLogEntry> fullLog = log.toList();
        if (fullLog != null) {
            for (VisitLogEntry e : fullLog) {
                if (naive.contains(e.note(), pattern)) {
                    results.add(e);
                }
            }
        }
        return results;
    }

    @Override
    public List<VisitLogEntry> searchLogKmp(String pattern) {
        List<VisitLogEntry> results = new ArrayList<>();
        List<VisitLogEntry> fullLog = log.toList();
        if (fullLog != null) {
            for (VisitLogEntry e : fullLog) {
                if (kmp.contains(e.note(), pattern)) {
                    results.add(e);
                }
            }
        }
        return results;
    }

    @Override
    public Result<Action> undo() {
        return Result.fail("Undo operation not initialized");
    }

    private String newAppointmentId() {
        return "A" + (nextApptId++);
    }
}