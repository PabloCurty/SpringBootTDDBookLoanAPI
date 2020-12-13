package com.curty.libraryAPI.service;

import com.curty.libraryAPI.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOAN = "0 0 0 1/1 * ?";

    @Value("${application.mail.late.loans.message}")
    private String message;

    private final LoanService loanService;

    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOAN)
    public void sendEmailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());
        emailService.sendMails(message, mailsList);
    }
}
