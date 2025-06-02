package code_security.coin_futures.service.KeyStoreService;

import code_security.coin_futures.crypto.CryptoUtil;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class KeyStoreService {

    // 서버용 공개키 (단일 인스턴스에서 static으로 고정)
    private final KeyPair serverKeyPair;
    private final Map<Long, KeyPair> userKeyPairs = new ConcurrentHashMap<>();

    public KeyStoreService(){
        try {
            this.serverKeyPair = CryptoUtil.generateRSAKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("서버 RSA 키 쌍 생성 실패", e);
        }
    }

    public PublicKey getServerPublicKey() {

        return serverKeyPair.getPublic();
    }

    public PrivateKey getUserPrivateKey(Long userId) {
        return userKeyPairs.computeIfAbsent(userId, k -> {
            try {
                return CryptoUtil.generateRSAKeyPair();
            } catch (Exception e) {
                throw new RuntimeException("사용자 키 쌍 생성 실패", e);
            }
        }).getPrivate();
    }
    public PublicKey getUserPublicKey(Long userId) {

        KeyPair keyPair = userKeyPairs.get(userId);
        if (keyPair == null) {
            throw new IllegalStateException("사용자 키 쌍이 존재하지 않습니다.");
        }
        return keyPair.getPublic();
    }
}
