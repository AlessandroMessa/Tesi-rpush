package com.regent.rpush.server.socket.session.lifecycle;

import com.regent.rpush.api.route.RpushServerOnlineService;
import com.regent.rpush.dto.rpushserver.LoginDTO;
import com.regent.rpush.dto.rpushserver.OfflineDTO;
import com.regent.rpush.dto.rpushserver.ServerInfoDTO;
import com.regent.rpush.server.socket.session.SocketSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Gestisce il ciclo di vita delle sessioni: login e offline.
 */
@Service
public class SessionLifecycleService {

    private final RpushServerOnlineService onlineService;
    private final ObjectProvider<ServerInfoDTO> serverInfoProvider;

    @Autowired
    public SessionLifecycleService(RpushServerOnlineService onlineService,
                                   ObjectProvider<ServerInfoDTO> serverInfoProvider) {
        this.onlineService = onlineService;
        this.serverInfoProvider = serverInfoProvider;
    }

    /**
     * Effettua il login della sessione sul server e persiste lo stato online.
     * @param registrationId l'id di registrazione client
     * @param session         la sessione socket
     */
    public void login(Long registrationId, SocketSession session) {
        session.setRegistrationId(registrationId);
        ServerInfoDTO serverInfo = serverInfoProvider.getIfAvailable();
        onlineService.login(
                LoginDTO.builder()
                        .registrationId(registrationId)
                        .serverInfo(serverInfo)
                        .build()
        );
    }

    /**
     * Effettua il logout della sessione e persiste lo stato offline.
     * @param session la sessione socket
     */
    public void offline(SocketSession session) {
        Long regId = session.getRegistrationId();
        if (regId != null) {
            onlineService.offline(
                    OfflineDTO.builder()
                            .registrationId(regId)
                            .build()
            );
        }
    }
}
