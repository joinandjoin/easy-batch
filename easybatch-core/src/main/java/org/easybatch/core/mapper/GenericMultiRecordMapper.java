/*
 *  The MIT License
 *
 *   Copyright (c) 2015, Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package org.easybatch.core.mapper;

import org.easybatch.core.api.Record;
import org.easybatch.core.api.RecordMapper;
import org.easybatch.core.api.RecordMappingException;
import org.easybatch.core.record.MultiRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * This mapper extracts the list of objects contained in a MultiRecord.
 * <p/>
 * Example: Let MR = MultiRecord {
 * <p/>
 * record1: StringRecord with payload "foo",
 * <p/>
 * record2: StringRecord with payload "bar"
 * <p/>
 * }
 * <p/>
 * GenericMultiRecordMapper.mapRecord(MR) yields in ["foo", "bar"]
 *
 * @author Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 */
public class GenericMultiRecordMapper<P> implements RecordMapper<List<P>> {

    @Override
    public List<P> mapRecord(final Record record) throws RecordMappingException {
        MultiRecord multiRecord = (MultiRecord) record;
        List<Record> records = multiRecord.getPayload();
        List<P> payloads = new ArrayList<P>();
        for (Record r : records) {
            payloads.add((P) r.getPayload());

        }
        return payloads;
    }
}
