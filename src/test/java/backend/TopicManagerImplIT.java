package backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import backend.exception.InvalidUriInputException;
import java.util.List;
import java.util.function.Function;
import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

class TopicManagerImplIT {

  private static final String RESOURCE_URI = "http://dbpedia.org/resource/";

  @Test
  void getSuggestionsForCurrentTopicFindsCorrectNumberOfDestinationsAfterAddingSomeResources()
      throws InvalidUriInputException {
    // given
    final int nProposals = 8;
    final TopicManagerImpl cut = new TopicManagerImpl();
    cut.addResourceToTopics(RESOURCE_URI + "Mannheim");
    final String currentResource = RESOURCE_URI + "SAP_Arena";
    cut.addResourceToTopics(currentResource);
    // when
    final List<QuerySolution> result = cut.getSuggestionsForCurrentTopic(nProposals);
    // then
    assertThat(result).hasSize(nProposals)
        .allSatisfy(resultBinding -> assertThat(
            List.of("uri", "sample_property", "label").stream().map((Function<String, Object>) resultBinding::contains))
            .as("each result set must contain the variables 'new_word' and 'sample_property'")
            .containsOnly(true))
        .as("some proposal must have label '2010_IIHF_World_Championship' and corresponding URI")
        .anySatisfy(resultBinding -> {
          final String proposal = "2010_IIHF_World_Championship";
          assertThat(resultBinding.get("uri").toString().contains(proposal))
              .isTrue();
          assertThat(resultBinding.get("label").asLiteral().getString()).isEqualTo(proposal.replace("_", " "));
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
        .satisfies(x -> assertThat(x.memoryModel.size()).isEqualTo(577))
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
