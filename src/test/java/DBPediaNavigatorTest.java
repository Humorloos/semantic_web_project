import org.apache.jena.query.QuerySolution;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DBPediaNavigatorTest {

    @Test
    void registerNewResource() {
        // given
        DBPediaNavigator cut = new DBPediaNavigator();
        // when
        cut.registerNewResource("Mannheim");
        // then
        // when
        cut.registerNewResource("Ernst_Gaber");
        // then
        // when
        List<QuerySolution> result = cut.findNextDestinations();
        assertThat(result).hasSize(568);
    }
}