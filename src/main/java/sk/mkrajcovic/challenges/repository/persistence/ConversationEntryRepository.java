package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.ConversationEntry;

@Repository
public interface ConversationEntryRepository extends JpaRepository<ConversationEntry, Integer> {

	public boolean existsByIdAndChallengeId(Integer id, Integer challengeId);

	public List<ConversationEntry> findAllByParentEntryId(Integer parentEntryId);

}
