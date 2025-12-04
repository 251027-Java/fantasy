package scripts;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import java.util.EnumSet;

public class GenerateDdl {
    static void main(String[] args) {
        Logging.disableLogging();

        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .build();

        // register classes here
        var metadata = new MetadataSources(registry).buildMetadata();

        var schemaExport = new SchemaExport();
        schemaExport.setOutputFile(args[0]);
        schemaExport.setFormat(true);
        schemaExport.setDelimiter(";");
        schemaExport.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata);
    }
}
