package server.service;

import common.Payment;
import server.repository.Repository;

public class PaymentService {

    private Repository<Payment, Long> paymentRepository;

    public PaymentService(Repository<Payment, Long> paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public void makePayment(Payment payment){
        paymentRepository.add(payment);
    }
}
