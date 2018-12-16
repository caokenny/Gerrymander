package io.redistrict.auth.repository;

import io.redistrict.auth.model.SavedWeights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface SavedWeightsDao extends JpaRepository<SavedWeights, Integer> {
    SavedWeights findById(int id);
}
