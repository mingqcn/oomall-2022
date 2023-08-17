--KEYS
--所有BanSet的名称

--ARGV
--要检查是否被ban的jwt

local jwt = ARGV[1]

for index, value in ipairs(KEYS) do
    local setName = value
    if(1 == redis.call('EXISTS', setName)) then
        if(1 == redis.call('SISMEMBER', setName, jwt)) then
            return true
        end
    end
end

return false