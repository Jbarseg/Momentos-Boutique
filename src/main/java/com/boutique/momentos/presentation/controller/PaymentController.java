package com.boutique.momentos.presentation.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.boutique.momentos.domain.domainentity.PaymentDomain;
import com.boutique.momentos.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<PaymentDomain>> getAllPayments() {
        List<PaymentDomain> payments = paymentService.getAll();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Obtener un pago por su ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDomain> getPaymentById(@PathVariable("id") int id) {
        Optional<PaymentDomain> payment = paymentService.getPayment(id);
        return payment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener todos los pagos asociados a un usuario específico
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDomain>> getPaymentsByUser(@PathVariable("userId") int userId) {
        Optional<List<PaymentDomain>> payments = paymentService.getByUser(userId);
        return payments.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Guardar un nuevo pago
    @PostMapping("/upload")
    public ResponseEntity<PaymentDomain> savePayment(@RequestParam("userId") int userId,
                                                     @RequestParam("image") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] imageData = imageFile.getBytes();
            PaymentDomain payment = new PaymentDomain();
            payment.setDomainIdUser(userId);
            payment.setDomainImageData(imageData);
            PaymentDomain savedPayment = paymentService.savePayment(payment);
            return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar un pago por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable("id") int id) {
        boolean deleted = paymentService.delete(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}