package com.kpanov.transfer.service;

import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.service.model.TransferResultModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository repository;

    @Transactional
    public TransferResultModel transfer(
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {

    }
}