package com.example.subscriber.repositories;

import com.example.subscriber.entities.Packet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacketsRepository extends MongoRepository<Packet, String> {
}
