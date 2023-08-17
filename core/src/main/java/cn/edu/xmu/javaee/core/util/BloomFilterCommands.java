package cn.edu.xmu.javaee.core.util;

import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import io.lettuce.core.dynamic.annotation.Param;

@CommandNaming(strategy = CommandNaming.Strategy.DOT)
public interface BloomFilterCommands extends Commands {

    Boolean del(String key);

//    @Command("BF.RESERVE ?0 ?1 ?2")
//    Boolean bfReserve(String key, String errorRate, String capacity);

    Boolean bfAdd(String key,String value);

    @Command("BF.EXISTS :key :value")
    Boolean bfExists(@Param("key") String key, @Param("value") String value);
}
