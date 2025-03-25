CREATE OR REPLACE FUNCTION most_common_text_calculate(storage most_common_text_data)
RETURNS NULL ON NULL INPUT
RETURNS text
LANGUAGE lua AS $$
    if storage == nil or storage.text_data == nil then
        return nil
    end
    local value = nil
    local count = 0
    for v, c in pairs(storage.text_data) do
        if c > count then
            value = v
            count = c
        end
    end
    return value
$$;
