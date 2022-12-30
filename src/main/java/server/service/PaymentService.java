package server.service;

import common.Payment;
import server.repository.Repository;

import java.util.Collection;

public class PaymentService {

    private Repository<Payment, Long> paymentRepository;

    public PaymentService(Repository<Payment, Long> paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(Payment payment){
        paymentRepository.add(payment);
        return payment;
    }

    public Collection<Payment> getALlPayments(){
        return paymentRepository.getAll();
    }
}
