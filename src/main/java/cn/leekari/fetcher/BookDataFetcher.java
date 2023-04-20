package cn.leekari.fetcher;

import cn.leekari.entity.User;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class BookDataFetcher implements DataFetcher<User> {

//    private final Map<String, User> data = Map.of("1", new User("1", "hero"),
//            "2", new User("2", "human"),
//            "3", new User("3", "droid"));

    @Override
    public User get(DataFetchingEnvironment environment) throws Exception {
        final String id = environment.getArgument("id");
        return new User();
    }
}