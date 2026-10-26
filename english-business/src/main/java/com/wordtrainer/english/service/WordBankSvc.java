package com.wordtrainer.english.service;

import com.wordtrainer.english.dto.WordCreateCommand;
import com.wordtrainer.english.dto.WordPickPoolView;
import com.wordtrainer.english.dto.WordUpdateCommand;
import com.wordtrainer.english.dto.WordWrongView;
import com.wordtrainer.english.entity.WordBank;
import java.util.List;

/**
 * Service interface for managing words in the Word Bank.
 * Provides methods for CRUD operations, batch creation,
 * statistics, and weighted word selection for exercises.
 */
public interface WordBankSvc {

  /**
   * Creates a single word entry in the Word Bank.
   *
   * @param cmd the command containing the word data
   * @return the saved {@link WordBank} entity
   */
  WordBank createOne(WordCreateCommand cmd);

  /**
   * Creates multiple word entries in a batch operation.
   *
   * @param cmds a list of commands containing word data
   * @return a list of saved {@link WordBank} entities
   */
  List<WordBank> createBatch(List<WordCreateCommand> cmds);

  /**
   * Retrieves all words in the Word Bank.
   *
   * @return a list of all {@link WordBank} entities
   */
  List<WordBank> findAll();

  /**
   * Updates an existing word entry.
   *
   * @param id  the ID of the word to update
   * @param cmd the command containing updated word data
   * @return the updated {@link WordBank} entity
   */
  WordBank update(Long id, WordUpdateCommand cmd);

  /**
   * Deletes a word entry by its ID.
   * Does nothing if the word does not exist.
   *
   * @param id the ID of the word to delete
   */
  void deleteOne(Long id);

  /**
   * Retrieves a list of words by their IDs.
   * Returns an empty list if the input list is null or empty.
   *
   * @param ids the list of word IDs to retrieve
   * @return a list of {@link WordBank} entities
   */
  List<WordBank> findAllByIds(List<Long> ids);

  /**
   * Atomically increments the exam pass count for a word.
   * Throws an exception if the word does not exist.
   *
   * @param wordId the ID of the word
   * @param delta  the amount to increment (can be negative)
   */
  void addExamPassCount(Long wordId, int delta);

  /**
   * Retrieves statistics about incorrectly answered words.
   * Returns a list of projections used for analytics or reporting.
   *
   * @return a list of {@link WordWrongView} projections
   */
  List<WordWrongView> findWrongWordStats();

  /**
   * Retrieves a weighted selection pool of words for exercises.
   * Weighting is based on memorization level, exam pass count, and past wrong answers.
   *
   * @return a list of {@link WordPickPoolView} DTOs with weights applied
   */
  List<WordPickPoolView> getPickPool();
}
