/*
 * Copyright (c) 2017, Red Hat Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.sample;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

// See jcstress-samples or existing tests for API introduction and testing guidelines

@JCStressTest
// Outline the outcomes here. The default outcome is provided, you need to remove it:
@Outcome(id = "30, 0", expect = Expect.ACCEPTABLE, desc = "(volatile x) actor 2 completely before actor 1")
@Outcome(id = "30, 10", expect = Expect.ACCEPTABLE_INTERESTING, desc = "(volatile x) actor 2 partially after actor 1")
@Outcome(id = "30, 30", expect = Expect.ACCEPTABLE, desc = "(volatile x) actor 2 completely after actor 1")
@Outcome(id = "30, 20", expect = Expect.FORBIDDEN, desc = "(volatile x) actor 2 sees y = 20 and x = 0")
@State
public class ConcurrencyVolatileX {

    FieldsNextToEachOther fields = new FieldsNextToEachOther();

    @State
    public static class FieldsNextToEachOther {
        volatile int x;
        int y;
    }

    @Actor
    public void actor1(II_Result r) {
        fields.x = 10;
        fields.y = 20;
        r.r1 = fields.x + fields.y;
    }

    @Actor
    public void actor2(II_Result r) {
        int x = fields.x;
        int y = fields.y;
        r.r2 = x + y;
    }

}
