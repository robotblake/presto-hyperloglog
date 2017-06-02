/*
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

package com.mozilla.presto.hyperloglog;

import com.facebook.presto.spi.block.BlockBuilder;
import com.facebook.presto.spi.function.AccumulatorStateSerializer;
import com.facebook.presto.spi.function.AggregationFunction;
import com.facebook.presto.spi.function.AggregationState;
import com.facebook.presto.spi.function.CombineFunction;
import com.facebook.presto.spi.function.InputFunction;
import com.facebook.presto.spi.function.OutputFunction;
import com.facebook.presto.spi.function.SqlType;
import com.twitter.algebird.DenseHLL;
import com.twitter.algebird.HyperLogLog;
import io.airlift.slice.Slice;

@AggregationFunction("merge")
public final class HyperLogLogAggregation
{
    private static final AccumulatorStateSerializer<HyperLogLogState> serializer = new HyperLogLogStateSerializer();

    private HyperLogLogAggregation() {}

    @InputFunction
    public static void input(@AggregationState HyperLogLogState state, @SqlType(HyperLogLogType.TYPE) Slice value)
    {
        DenseHLL input = HyperLogLog.fromBytes(value.getBytes()).toDenseHLL();
        merge(state, input);
    }

    @CombineFunction
    public static void combine(@AggregationState HyperLogLogState state, @AggregationState HyperLogLogState otherState)
    {
        merge(state, otherState.getHyperLogLog());
    }

    private static void merge(@AggregationState HyperLogLogState state, DenseHLL input)
    {
        DenseHLL previous = state.getHyperLogLog();
        if (previous == null) {
            state.setHyperLogLog(input);
            state.addMemoryUsage(input.size());
        }
        else {
            input.updateInto(previous.v().array());
        }
    }

    @OutputFunction(HyperLogLogType.TYPE)
    public static void output(@AggregationState HyperLogLogState state, BlockBuilder out)
    {
        serializer.serialize(state, out);
    }
}
