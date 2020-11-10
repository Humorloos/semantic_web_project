package backend;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

class TopicManagerImplIT {

  private static final String RESOURCE_URI = "http://dbpedia.org/resource/";

  @Test
  void findNextDestinationsFindsCorrectNumberOfDestinationsAfterAddingSomeResources() {
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
            List.of("new_word", "sample_property").stream().map((Function<String, Object>) resultBinding::contains))
            .as("each result set must contain the variables 'new_word' and 'sample_property'")
            .containsOnly(true))
        .anySatisfy(resultBinding -> assertThat(resultBinding.get("new_word").toString().contains("2014–15_DEL_season"))
            .as("some proposal must contain the resource '2014-15_DEL_season'")
            .isTrue());
  }
}
