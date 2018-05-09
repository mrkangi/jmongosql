package com.mrkangi.jmongosql;

import com.alibaba.fastjson.JSON;
import lombok.experimental.var;
import org.junit.jupiter.api.Test;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ConditionBuilderTest {
    class Tuple<A, B> {
        public final A first;
        public final B second;

        public Tuple(A a, B b) {
            this.first = a;
            this.second = b;
        }
    }
    @Test
    public void build() {
        List<Tuple<String, String>> testExample = new ArrayList<>();
        testExample.add(new Tuple<String, String>("{ id: 5 }", "`users`.`id` = $1"));
        testExample.add(new Tuple<String, String>("{ id: 5, name: 'Bob' }", "`users`.`id` = $1 and `users`.`name` = $2"));
        testExample.add(new Tuple<String, String>("{ isAwesome: true }", "`users`.`isAwesome` is true"));
        testExample.add(new Tuple<String, String>("{ isAwesome: false }", "`users`.`isAwesome` is false"));
        testExample.add(new Tuple<String, String>("{ '$or': { id: 5, name: 'Bob' } }", "`users`.`id` = $1 or `users`.`name` = $2"));
        testExample.add(new Tuple<String, String>("{ id: { '$gt': 5 } }", "`users`.`id` > $1"));
        testExample.add(new Tuple<String, String>("{ '$gt': { id: 5, name: 'Tobias' } }", "`users`.`id` > $1 and `users`.`name` > $2"));
        testExample.add(new Tuple<String, String>("{ id: { '$gte': 5 } }", "`users`.`id` >= $1"));
        testExample.add(new Tuple<String, String>("{ '$gte': { id: 5, name: 'Tobias' } }", "`users`.`id` >= $1 and `users`.`name` >= $2"));
        testExample.add(new Tuple<String, String>("{ id: { '$lt': 5 } }", "`users`.`id` < $1"));
        testExample.add(new Tuple<String, String>("{ '$lt': { id: 5, name: 'Tobias' } }", "`users`.`id` < $1 and `users`.`name` < $2"));
        testExample.add(new Tuple<String, String>("{ '$lte': { id: 5, name: 'Tobias' } }", "`users`.`id` <= $1 and `users`.`name` <= $2"));
        testExample.add(new Tuple<String, String>("{ id: { '$null': true } }", "`users`.`id` is null"));
        testExample.add(new Tuple<String, String>("{ id: { '$null': false } }", "`users`.`id` is not null"));
        testExample.add(new Tuple<String, String>("{ id: null }", "`users`.`id` is null"));
        testExample.add(new Tuple<String, String>("{ id: { '$notNull': true } }", "`users`.`id` is not null"));
        testExample.add(new Tuple<String, String>("{ id: { '$notNull': false } }", "`users`.`id` is null"));
        testExample.add(new Tuple<String, String>("{ name: { '$like': 'Bobo' } }", "`users`.`name` like $1"));
        testExample.add(new Tuple<String, String>("{ name: { '$ilike': 'Bobo' } }", "`users`.`name` ilike $1"));
        testExample.add(new Tuple<String, String>("{id: {'$in': [1, 2, 3]}}", "`users`.`id` in ($1, $2, $3)"));
        testExample.add(new Tuple<String, String>("{id: {'$nin': [1, 2, 3]}}", "`users`.`id` not in ($1, $2, $3)"));
        testExample.add(new Tuple<String, String>("{name: 'Bob', id: { '$lt': 500 }, groupId: { '$equals': 7 }, another: { '$or': [1, 2, 3, 4] }}", "`users`.`name` = $1 and `users`.`id` < $2 and `users`.`groupId` = $3 and (`users`.`another` = $4 or `users`.`another` = $5 or `users`.`another` = $6 or `users`.`another` = $7)"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$years_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 year"));
        testExample.add(new Tuple<String, String>("{'$years_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 year and `users`.`somethingElse` >= now() - interval $2 year"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$months_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 month"));
        testExample.add(new Tuple<String, String>("{'$months_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 month and `users`.`somethingElse` >= now() - interval $2 month"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$days_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 day"));
        testExample.add(new Tuple<String, String>("{'$days_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 day and `users`.`somethingElse` >= now() - interval $2 day"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$hours_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 hour"));
        testExample.add(new Tuple<String, String>("{'$hours_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 hour and `users`.`somethingElse` >= now() - interval $2 hour"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$minutes_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 minute"));
        testExample.add(new Tuple<String, String>("{'$minutes_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 minute and `users`.`somethingElse` >= now() - interval $2 minute"));
        testExample.add(new Tuple<String, String>("{createdAt: { '$seconds_ago': 2 }}", "`users`.`createdAt` >= now() - interval $1 second"));
        testExample.add(new Tuple<String, String>("{'$seconds_ago': { createdAt: 2, somethingElse: 3 }}", "`users`.`createdAt` >= now() - interval $1 second and `users`.`somethingElse` >= now() - interval $2 second"));
        testExample.add(new Tuple<String, String>("{}", ""));


        List<String> table = new ArrayList<>();
        table.add("users");
        for (Tuple<String,String> item : testExample)
        {
            ConditionBuilder cb = new ConditionBuilder(item.first, table,null);
            String build = cb.build();
            System.out.println(item.first + "\t =====> \t" + build);
            Boolean ok = build.equals(item.second);
            System.out.println(String.join(",", cb.getParameters().stream().map(p->p.toString()).collect(Collectors.toList())));
            if(!ok)
                System.err.println("=====///// "+item.first + " /////====" +item.second +" \\\\\\\\\\=======" + build);
        }
    }
}