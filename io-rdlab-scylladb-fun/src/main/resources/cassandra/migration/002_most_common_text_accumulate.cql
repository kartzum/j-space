CREATE OR REPLACE FUNCTION most_common_text_accumulate(storage most_common_text_data, val text)
RETURNS NULL ON NULL INPUT
RETURNS most_common_text_data
LANGUAGE lua
AS $$
    if storage == nil then
        storage = {}
    end
    if storage.text_data == nil then
        storage.text_data = {}
    end
    if val == nil then
        return storage
    end
    if storage.text_data[val] == nil then
        storage.text_data[val] = 1
    else
        storage.text_data[val] = storage.text_data[val] + 1
    end
    return storage
$$;
