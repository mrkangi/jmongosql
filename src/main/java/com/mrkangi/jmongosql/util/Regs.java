package com.mrkangi.jmongosql.util;

import java.util.regex.Pattern;

public class Regs
{
    public static final Pattern DereferenceOperators = Pattern.compile("[-#=]+>+");
    public static final Pattern EndsInCast = Pattern.compile("::\\w+$");
}
