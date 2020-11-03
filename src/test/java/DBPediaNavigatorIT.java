import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DBPediaNavigatorIT {

    @Test
    void findNextDestinationsFindsCorrectNumberOfDestinationsAfterAddingSomeResources() {
        // given
        DBPediaNavigator cut = new DBPediaNavigator();
        // when
        cut.registerNewResource("Mannheim");
        cut.registerNewResource("SAP_Arena");
        List<QuerySolution> result = cut.findNextDestinations();
        // then
        assertThat(result).hasSize(571);
    }
}
