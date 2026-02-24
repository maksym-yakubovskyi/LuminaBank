package com.lumina_bank.accountservice.application.service;

import com.lumina_bank.accountservice.api.request.CardCreateRequest;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.accountservice.domain.exception.AccountLockedException;
import com.lumina_bank.accountservice.domain.exception.CardNotFoundException;
import com.lumina_bank.accountservice.domain.model.Account;
import com.lumina_bank.accountservice.domain.model.Card;
import com.lumina_bank.accountservice.domain.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final AccountService accountService;

    @Transactional
    public Card createCard(Long accountId, CardCreateRequest request) {
        Account account = accountService.getAccountById(accountId);

        validateAccountActive(account);

        String cardNumber = generateUniqueCardNumber();

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .expirationDate(YearMonth.now().plusYears(4)) // TODO: можливо доробити щоб не завжди 4 роки термін дії
                .cvv(generateCVV())
                .cardType(request.cardType())
                .cardNetwork(request.cardNetwork())
                .status(Status.ACTIVE)
                .limit(request.limit())
                .account(account)
                .build();

        Card saved = cardRepository.save(card);

        log.debug("Card entity persisted cardId={} accountId={}", saved.getId(), accountId);

        return saved;
    }

    @Transactional
    public Card setStatus(Long cardId, Status status) {
        Card card = getCardById(cardId);

        card.setStatus(status);

        log.debug("Card status updated cardId={} status={}", cardId, status);

        return card;
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByAccountId(Long accountId) {
        Account account = accountService.getAccountById(accountId);

        return cardRepository.findAllByAccount(account);
    }

    @Transactional(readOnly = true)
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByUserId(Long userId) {
        return cardRepository.findAllByAccount_UserId(userId);
    }

    // PRIVATE HELPERS

    private void validateAccountActive(Account account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountLockedException("Account is not active");
        }
    }

    private String generateUniqueCardNumber() {
        String number;
        do {
            number = generateCardNumber();
        } while (cardRepository.existsByCardNumber(number));
        return number;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCVV() {
        // Формуємо число від 0 до 999, і завжди отримуємо 3 символи (з ведучими нулями)
        return String.format("%03d", new Random().nextInt(1000));
    }
}