package com.example.blps_version_saver.service;

import com.atomikos.icatch.jta.UserTransactionImp;

import com.example.blps_version_saver.model.VersionEntity;
import com.example.blps_version_saver.repository.SectionRepo;
import com.example.blps_version_saver.repository.VersionRepo;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class VersionSaveServiceImpl implements VersionSaveService{
    private final UserTransactionImp utx;
    private final AtomikosDataSourceBean dataSource;
    private final JmsTemplate jmsTemplate;
    private final VersionRepo repo;
    private final SectionRepo sectionRepo;

    public VersionSaveServiceImpl(UserTransactionImp utx, AtomikosDataSourceBean dataSource, JmsTemplate jmsTemplate, VersionRepo repo, SectionRepo sectionRepo) {
        this.utx = utx;
        this.dataSource = dataSource;
        this.jmsTemplate = jmsTemplate;
        this.repo = repo;
        this.sectionRepo = sectionRepo;
    }

    private VersionEntity getEntityFromMessage(Map<String, Object> message){
        VersionEntity entity = new VersionEntity();
        entity.setSectionId((Integer) message.get("sectionId"));
        entity.setPersonedited((String) message.get("personEdited"));
        entity.setSectiontext((String) message.get("sectionText"));
        entity.setStatus((String) message.get("status"));
        return entity;
    }

    @Override
    @JmsListener(destination = "${queueName}", containerFactory = "myFactory")
    public void saveChanges(Map<String, Object> message) throws Exception {
        boolean rollback = false;
        try {
            utx.begin();
            Connection connection = dataSource.getConnection();
            VersionEntity entity = getEntityFromMessage(message);
            repo.save(entity);
            sectionRepo.updateText(entity.getSectiontext(), entity.getSectionId());

            connection.close();
        } catch (Exception e) {
            rollback = true;
            e.printStackTrace();
        } finally {
            if (!rollback) utx.commit();
            else utx.rollback();
        }
    }

}
