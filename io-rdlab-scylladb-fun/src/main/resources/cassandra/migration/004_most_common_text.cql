CREATE OR REPLACE AGGREGATE most_common_text(text)
   SFUNC most_common_text_accumulate
   STYPE most_common_text_data
   FINALFUNC most_common_text_calculate
   INITCOND {text_data: {}};
