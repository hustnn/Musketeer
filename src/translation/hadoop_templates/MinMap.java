      if ({{CONDITION}}) {
        {{COL_TYPE}} col_value = {{COL_TYPE}}.valueOf({{REL_NAME}}[{{COL_INDEX}}]);
        if ({{GROUP_BY}} == -1) {
          if (minValue == null || minValue.compareTo(col_value) > 0) {
            minValue = col_value;
            {{GEN_SELECTED_COLS}}
          }
        } else {
          Text newKey = new Text({{GROUP_BY_KEY}});
          {{GEN_SELECTED_COLS}}
          context.write(newKey,
                        new Text(String.valueOf(col_value) + selectedCols));
        }
      }
