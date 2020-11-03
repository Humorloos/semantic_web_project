import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DBPediaNavigatorIT {

    @Test
    void findNextDestinationsFindsCorrectNumberOfDestinationsAfterAddingSomeResources() {
        // given
        final DBPediaNavigator cut = new DBPediaNavigator();
        cut.registerNewResource("Mannheim");
        cut.registerNewResource("SAP_Arena");
        // when
        final List<QuerySolution> result = cut.findNextDestinations();
        // then
        assertThat(result).hasSize(571);
    }
}
