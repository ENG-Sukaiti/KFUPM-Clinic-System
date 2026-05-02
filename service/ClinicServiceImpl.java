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

    // Hash tables
    private final HashTable<String, Patient> patientsById = new HashTable<>();
    private final HashTable<String, Appointment> apptsById = new HashTable<>();

    // Appointment schedule index (AVL)
    private final AVLTree<AppointmentKey, Appointment> apptsByTime = new AVLTree<>();

    // Walk-ins and urgent
    private final LinkedQueue<Patient> walkIns = new LinkedQueue<>();
    private final MaxHeap<UrgentPatient> urgentHeap = new MaxHeap<>((a, b) -> {
        // Higher severity first; tie-break earlier arrival first.
        if (a.severity() != b.severity()) return Integer.compare(a.severity(), b.severity());
        // earlier arrival should win => invert compare so earlier is "greater"
        return Long.compare(b.arrivalEpochMillis(), a.arrivalEpochMillis());
    });

    // Undo + log
    private final LinkedStack<Action> undo = new LinkedStack<>();
    private final SinglyLinkedList<VisitLogEntry> log = new SinglyLinkedList<>();

    private final StringMatcher naive = new NaiveMatcher();
    private final StringMatcher kmp = new KMPMatcher();

    private int nextApptId = 1;

    @Override
    public Result<Void> addPatient(String id, String name, String phone) {
        // TODO: validate, check duplicates using hash table, insert, record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.addPatient");
    }

    @Override
    public Result<Patient> findPatient(String id) {
        // TODO: use hash table get
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.findPatient");
    }

    @Override
    public Result<Void> deletePatient(String id) {
        // TODO: remove from hash table, record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.deletePatient");
    }

    @Override
    public Result<String> addAppointment(String patientId, LocalDate date, LocalTime time, String doctor) {
        // TODO: ensure patient exists; create appointmentId; insert into AVL + hash; record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.addAppointment");
    }

    @Override
    public Result<Void> cancelAppointment(String appointmentId) {
        // TODO: use hash to find appt; remove from AVL + hash; record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.cancelAppointment");
    }

    @Override
    public Result<Appointment> findAppointment(String appointmentId) {
        // TODO: use hash table
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.findAppointment");
    }

    @Override
    public List<Appointment> viewDay(LocalDate date) {
        // TODO: in-order traverse AVL and filter by date, OR implement date range traversal
        return new ArrayList<>();
    }

    @Override
    public List<Appointment> viewRange(LocalDate date, LocalTime start, LocalTime end) {
        // TODO: range query traversal on AVL for (date,start) .. (date,end)
        return new ArrayList<>();
    }

    @Override
    public Result<Void> addWalkIn(String patientId) {
        // TODO: ensure patient exists; enqueue; record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.addWalkIn");
    }

    @Override
    public List<Patient> viewWalkIns() {
        // Non-destructive view
        return walkIns.toList();
    }

    @Override
    public Result<Void> addUrgent(String patientId, int severity) {
        // TODO: validate severity; ensure patient exists; heap push; record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.addUrgent");
    }

    @Override
    public Result<UrgentPatient> peekUrgent() {
        UrgentPatient up = urgentHeap.peek();
        if (up == null) return Result.fail("No urgent patients.");
        return Result.ok(up, "Most urgent patient.");
    }

    @Override
    public List<UrgentPatient> viewUrgentsSnapshot() {
        return urgentHeap.toListSnapshot();
    }

    @Override
    public Result<VisitLogEntry> serveNext(String doctor, String note) {
        // TODO: serving policy: urgent > walk-in > earliest appointment
        // TODO: append log entry, record undo
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.serveNext");
    }

    @Override
    public List<VisitLogEntry> printLog() {
        return log.toList();
    }

    @Override
    public List<VisitLogEntry> searchLogNaive(String pattern) {
        // TODO: iterate log entries; match pattern in note using NaiveMatcher
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.searchLogNaive");
    }

    @Override
    public List<VisitLogEntry> searchLogKmp(String pattern) {
        // TODO: iterate log entries; match pattern in note using KMPMatcher
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.searchLogKmp");
    }

    @Override
    public Result<Action> undo() {
        // TODO: pop undo stack and reverse last action
        throw new UnsupportedOperationException("TODO: ClinicServiceImpl.undo");
    }

    // Helpers you may want
    private String newAppointmentId() {
        return "A" + (nextApptId++);
    }
}
