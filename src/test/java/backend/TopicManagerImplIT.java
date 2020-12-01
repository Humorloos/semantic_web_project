package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import backend.exception.InvalidUriInputException;
import model.TopicInfo;

class TopicManagerImplIT {

  private static final String RESOURCE_URI = "http://dbpedia.org/resource/";

  @Test
  void getSuggestionsForCurrentTopicFindsCorrectNumberOfDestinationsAfterAddingSomeResources()
      throws InvalidUriInputException {
    // given
    final int nProposals = 20;
    final TopicManagerImpl cut = new TopicManagerImpl();
    cut.addResourceToTopics(RESOURCE_URI + "Mannheim");
    final String currentResource = RESOURCE_URI + "SAP_Arena";
    cut.addResourceToTopics(currentResource);
    // when
    final List<TopicInfo> result = cut.getSuggestionsForPreviousResources(nProposals);
    // then
    assertThat(result).hasSize(nProposals)
        .allSatisfy(resultBinding -> assertThat(resultBinding.getResourceUrl() != "" && resultBinding.getPropertyLabel() != "" && resultBinding.getLabel() != "")
            .as("each result set must contain the variables 'new_word' and 'sample_property'"))
        .as("some proposal must have label '2010_IIHF_World_Championship' and corresponding URI")
        .anySatisfy(resultBinding -> {
          final String proposal = "2010_IIHF_World_Championship";
          assertThat(resultBinding.getResourceUrl().contains(proposal));
          assertThat(resultBinding.getLabel()).isEqualTo(proposal.replace("_", " "));
          // Mannheim is actually described to be the Stadium of this Championship in DBPedia...
          assertThat(resultBinding.getPreviousResource().contains("Mannheim"));
        });
  }

  @Test
  void addResourceToTopicsAddsCorrectResourceInCaseOfValidInput() throws InvalidUriInputException {
    // given
    final TopicManagerImpl cut = new TopicManagerImpl();
    final String uri = RESOURCE_URI + "Mannheim";
    // when
    cut.addResourceToTopics(uri);
    // then
    assertThat(cut)
        .as("Memory model must be filled.")
        .satisfies(x -> assertThat(x.memoryModel.size()).isEqualTo(10147))
        .as("New uri must be added to previous resources.")
        .satisfies(x -> assertThat(x.previousResources).contains(uri))
        .as("Current topic must be set to new uri.")
        .satisfies(x -> assertThat(x.currentTopic).isEqualTo(uri));
  }

  @Test
  void addResourceToTopicsRaisesExceptionInCaseOfInvalidInput() {
    // given
    final TopicManagerImpl cut = new TopicManagerImpl();
    final String uri = RESOURCE_URI + "invalid_resource";
    // when
    final Throwable thrown = catchThrowable(() -> cut.addResourceToTopics(uri));
    // then
    assertThat(thrown)
        .isInstanceOf(InvalidUriInputException.class)
        .hasMessage(String.format("Resource '%s' not found.", uri));
  }
}
