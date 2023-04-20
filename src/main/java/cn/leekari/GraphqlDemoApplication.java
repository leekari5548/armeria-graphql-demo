package cn.leekari;


import cn.leekari.fetcher.BookDataFetcher;
import cn.leekari.fetcher.UserDataFetcher;
import cn.leekari.resolver.UserQuery;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.QueryParamsBuilder;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.graphql.GraphqlService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class GraphqlDemoApplication {

    public static void main(String[] args) {
        ServerBuilder sb = Server.builder();
        sb.port(8080, SessionProtocol.HTTP);
//        sb.virtualHost("test.com");
        sb.service("/graphql",
                GraphqlService.builder()
                        .schema(GraphQLSchema.newSchema().query(GraphQLObjectType.newObject().name("Query").build()).build())
                        .runtimeWiring(c -> {
                            c.type("Query",
                                    typeWiring -> typeWiring.dataFetcher("user", new UserDataFetcher()))
                                    .type("Query", typeWiring -> typeWiring.dataFetcher("book", new BookDataFetcher()));
                        })
                        .schemaUrls("src/main/resources/graphql/base.graphqls")
                        .schemaUrls("src/main/resources/graphql/book.graphqls")
                        .schemaUrls("src/main/resources/graphql/user.graphqls")
                        .build());
        sb.service(
                "/health/status",
                HealthCheckService.of());
        sb.annotatedService(new Object() {
            @Get("/greet/:name")
            public HttpResponse greet(@Param("name") String name) {
                return HttpResponse.of("Hello, %s!", name);
            }
        });
        sb.serviceUnder("/docs", new DocService());

        Server server = sb.build();
        server.start();
    }

}
