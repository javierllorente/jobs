/*
 * Copyright (C) 2020-2025 Javier Llorente <javier@opensuse.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javierllorente.jobs.util;

import jakarta.xml.bind.JAXB;
import java.io.StringWriter;

/**
 *
 * @author javier
 */
public class Utils {

    private Utils() {
    }    

    public static String entityToString(Object jaxbObject) {
        StringWriter sw = new StringWriter();
        JAXB.marshal(jaxbObject, sw);
        return sw.toString();        
    }
    
}
