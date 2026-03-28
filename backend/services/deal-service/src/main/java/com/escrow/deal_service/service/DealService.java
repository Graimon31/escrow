package com.escrow.deal_service.service;

import com.escrow.deal_service.client.EscrowAccountClient;
import com.escrow.deal_service.domain.Deal;
import com.escrow.deal_service.domain.DealRepository;
import com.escrow.deal_service.domain.DealState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DealService {

    private final DealRepository dealRepository;
    private final EscrowAccountClient escrowAccountClient;

    public DealService(DealRepository dealRepository, EscrowAccountClient escrowAccountClient) {
        this.dealRepository = dealRepository;
        this.escrowAccountClient = escrowAccountClient;
    }

    @Transactional
    public Deal create(String title, BigDecimal amount, String currency, String depositorUsername, String beneficiaryUsername) {
        Deal deal = new Deal();
        deal.setTitle(title);
        deal.setAmount(amount);
        deal.setCurrency(currency);
        deal.setDepositorUsername(depositorUsername);
        deal.setBeneficiaryUsername(beneficiaryUsername);
        deal.setState(DealState.DRAFT);
        return dealRepository.save(deal);
    }

    public List<Deal> list() {
        return dealRepository.findAll();
    }

    public Deal get(UUID id) {
        return dealRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Сделка не найдена"));
    }

    @Transactional
    public Deal agree(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.agree(deal.getState()));
        return dealRepository.save(deal);
    }

    @Transactional
    public Deal openEscrowAccount(UUID id, String token) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.openAccount(deal.getState()));
        escrowAccountClient.openAccount(deal.getId(), deal.getAmount(), deal.getCurrency(), token);
        deal.setState(DealStateMachine.awaitFunding(deal.getState()));
        return dealRepository.save(deal);
    }

    @Transactional
    public void markFundingProcessing(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.fundingProcessing(deal.getState()));
        dealRepository.save(deal);
    }

    @Transactional
    public void markFundsSecured(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.fundsSecured(deal.getState()));
        dealRepository.save(deal);
    }


    @Transactional
    public void markAwaitingBeneficiaryFulfillment(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.awaitingBeneficiaryFulfillment(deal.getState()));
        dealRepository.save(deal);
    }

    @Transactional
    public void markAwaitingDepositorReview(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.awaitingDepositorReview(deal.getState()));
        dealRepository.save(deal);
    }

    @Transactional
    public void markReleasePending(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.releasePending(deal.getState()));
        dealRepository.save(deal);
    }

    @Transactional
    public void markDisputed(UUID id) {
        Deal deal = get(id);
        deal.setState(DealStateMachine.disputed(deal.getState()));
        dealRepository.save(deal);
    }
}
