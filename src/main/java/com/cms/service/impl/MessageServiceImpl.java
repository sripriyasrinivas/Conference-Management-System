package com.cms.service.impl;

import com.cms.model.Message;
import com.cms.model.User;
import com.cms.repository.MessageRepository;
import com.cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message sendMessage(User sender, Long receiverId, String subject, String content) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .subject(subject)
                .content(content)
                .build();

        return messageRepository.save(msg);
    }

    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(m -> {
            m.setRead(true);
            messageRepository.save(m);
        });
    }

    @Transactional(readOnly = true)
    public List<Message> getInbox(User user) {
        List<Message> messages = messageRepository.findByReceiverOrderBySentAtDesc(user);
        messages.forEach(m -> { if (!m.isRead()) { m.setRead(true); messageRepository.save(m); } });
        return messages;
    }

    @Transactional(readOnly = true)
    public List<Message> getSent(User user) {
        return messageRepository.findBySenderOrderBySentAtDesc(user);
    }

    @Transactional(readOnly = true)
    public long countUnread(User user) {
        return messageRepository.countByReceiverAndReadFalse(user);
    }

    @Transactional(readOnly = true)
    public List<User> getConversationPartners(User user) {
        return messageRepository.findConversationPartners(user);
    }
}
