package cn.leekari.fetcher;

import cn.leekari.entity.User;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class UserDataFetcher implements DataFetcher<User> {


    @Override
    public User get(DataFetchingEnvironment environment) throws Exception {
        final String id = environment.getArgument("id");
        return new User();
    }
}