package com.example.catalogproject.repository;

import com.example.catalogproject.entity.StaffMessage;
import com.example.catalogproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StaffMessageRepository extends JpaRepository<StaffMessage, Integer> {
    List<StaffMessage> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1, User receiver2, User sender2);
}