class Solution:
    def triangleNumber(self, nums: List[int]) -> int:
        sum = 0
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                for k in range(j + 1, len(nums)):
                    if (nums[i] + nums[j] > nums[k] and nums[i] + nums[k] > nums[j] and nums[j] + nums[k] > nums[i]):
                        sum += 1
        return sum