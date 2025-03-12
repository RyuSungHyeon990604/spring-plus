package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.ManagerLog;
import org.example.expert.domain.manager.repository.SaveMangerLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerLogService {
    private final SaveMangerLogRepository saveMangerLogRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logging(AuthUser authUser, String action, String status) {
        ManagerLog log = new ManagerLog(authUser.getId(), action, status);
        saveMangerLogRepository.save(log);
    }
}
