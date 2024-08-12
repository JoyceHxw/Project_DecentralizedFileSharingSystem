<script setup>
import PageContainer from '@/components/PageContainer.vue'
</script>
<template>
  <page-container title="平台使用说明">
    <el-space direction="vertical" alignment="flex-start">
      <el-text class="mx-1" size="large"
        >欢迎使用去中心化文件共享平台！</el-text
      >
      <el-text class="mx-1">请首先部署swarm</el-text>
      <el-text class="mx-1"
        >节点构建，参考https://docs.ethswarm.org/docs/bee/installation/build-from-source从GitHub拉取代码手动编译，但需要拉取2.0.0-rc5版本</el-text
      >
      <el-text class="mx-1"
        >配置参数，参考https://docs.ethswarm.org/docs/bee/working-with-bee/configuration#sepolia-testnet-configuration</el-text
      >
      <el-text class="mx-1"
        >创建metamask账户，用password和swarm.key将bee节点的地址导入到metamask中
        从faucet水龙头中https://sepolia-faucet.pk910.de/#/接水，获取sepoliaETH代币。
        在网站https://app.uniswap.org/swap?outputCurrency=0x543dDb01Ba47acB11de34891cD86B675F04840db&inputCurrency=ETH上用sETH兑换sBZZ
      </el-text>
    </el-space>
  </page-container>
</template>
