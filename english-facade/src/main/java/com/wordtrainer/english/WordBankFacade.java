package com.wordtrainer.english;

import com.wordtrainer.english.dto.WordCreateCommand;
import com.wordtrainer.english.dto.WordPickPoolView;
import com.wordtrainer.english.dto.WordUpdateCommand;
import com.wordtrainer.english.entity.WordBank;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Facade for word bank related operations.
 * <p>
 * This interface defines application-level use cases for managing words, including creation,
 * update, deletion, bulk import and query operations.
 * </p>
 */
public interface WordBankFacade {

  /**
   * Creates a new word.
   *
   * @param req request data for creating a word
   * @return created word information
   */
  WordBank createOne(WordCreateCommand req);

  /**
   * Imports words from an Excel file.
   *
   * @param file excel file containing word data
   * @return list of imported words
   */
  List<WordBank> importExcel(MultipartFile file);

  /**
   * Retrieves all words.
   *
   * @return list of words, empty if none exist
   */
  List<WordBank> findAll();

  /**
   * Updates an existing word.
   *
   * @param id  identifier of the word to update
   * @param req request data for updating the word
   * @return updated word information
   */
  WordBank update(Long id, WordUpdateCommand req);

  /**
   * Deletes a word by its identifier.
   *
   * @param id identifier of the word to delete
   */
  void deleteOne(Long id);

  /**
   * Returns a pool of words for picking or selection use cases.
   *
   * @return list of selectable words
   */
  List<WordPickPoolView> getPickPool();
}
