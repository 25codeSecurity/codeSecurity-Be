package code_security.coin_futures.web.controller;

public class ContractException extends RuntimeException {
    public ContractException(String message) {
        super(message);
    }
}
