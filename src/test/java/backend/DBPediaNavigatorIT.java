package backend;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

class DBPediaNavigatorIT {

  @Test
  void findNextDestinationsFindsCorrectNumberOfDestinationsAfterAddingSomeResources() {
    // given
    final int nProposals = 8;
    final DBPediaNavigator cut = new DBPediaNavigator(nProposals);
    cut.registerNewResource("Mannheim");
    final String currentResource = "SAP_Arena";
    cut.registerNewResource(currentResource);
    // when
    final List<QuerySolution> result = cut.findNextProposals(currentResource);
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
