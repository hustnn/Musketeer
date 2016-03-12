// Copyright (c) 2015 Ionel Gog <ionel.gog@cl.cam.ac.uk>

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR
 * A PARTICULAR PURPOSE, MERCHANTABLITY OR NON-INFRINGEMENT.
 *
 * See the Apache Version 2.0 License for specific language governing
 * permissions and limitations under the License.
 */

#include "ir/sub_operator.h"

#include <limits>
#include <map>
#include <utility>
#include <vector>

namespace musketeer {
namespace ir {

  vector<Value*> SubOperator::get_values() {
    return values;
  }

  OperatorType SubOperator::get_type() {
    return SUB_OP;
  }

  pair<uint64_t, uint64_t> SubOperator::get_output_size(
      map<string, pair<uint64_t, uint64_t> >* rel_size) {
    vector<Relation*> rels = get_relations();
    string input_rel = rels[0]->get_name();
    pair<uint64_t, uint64_t> sub_rel_size;
    if (rel_size->find(input_rel) != rel_size->end()) {
      sub_rel_size = (*rel_size)[input_rel];
    } else {
      // This should not happen.
      LOG(INFO) << "Called out of order";
      sub_rel_size = make_pair(1, numeric_limits<uint64_t>::max());
    }
    return UpdateIfSmaller(get_output_relation()->get_name(), sub_rel_size,
                           rel_size);
  }

  bool SubOperator::mapOnly() {
    return true;
  }

  OperatorInterface* SubOperator::clone() {
    return new SubOperator(get_input_dir(), get_condition_tree(),
                           get_relations(), values, get_output_relation());
  }

} // namespace ir
} // namespace musketeer
