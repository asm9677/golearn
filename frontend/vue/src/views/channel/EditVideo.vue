<template>
	<div
		style="position: fixed; height:100%; width:100%; z-index: 9999; background-color:white; top:0;font-family: 'BMJUA', sans-serif"
		@click="focusOut"
	>
		<Navigation :index="index" @change="changeIndex" />

		<div
			style="position: absolute;top:0; left:60px; background-color:#1C1C26; width: calc(100% - 60px); height:100%;"
		>
			<Header @exportVideo="exportFlag = !exportFlag" />
			<v-layout wrap style="height: 50%" class="mt-3">
				<v-flex md6 lg6 xl6 class="pl-10">
					<Section :index="index" @change="changeIndex" />
				</v-flex>
				<v-flex md6 lg6 xl6 class="pr-10">
					<Aside :doExport="exportFlag" />
				</v-flex>
			</v-layout>
			<v-layout wrap style="height: calc(50% - 92px); margin-top:16px;">
				<Article />
			</v-layout>
		</div>
	</div>
</template>

<script>
import Header from '@/components/video/editor/Header.vue';
import Navigation from '@/components/video/editor/Navigation.vue';
import Section from '@/components/video/editor/Section.vue';
import Aside from '@/components/video/editor/Aside.vue';
import Article from '@/components/video/editor/Article.vue';
import EventBus from '@/util/EventBus.js';
export default {
	name: 'EditVideo',
	components: {
		Header,
		Navigation,
		Section,
		Aside,
		Article,
	},
	data() {
		return {
			index: 0,
			exportFlag: false,
		};
	},
	mounted() {
		if (!this.$store.getters.isLogin) this.$router.push('/login');
		this.$store.dispatch('initEditVideo');
	},
	methods: {
		changeIndex(index) {
			this.index = index;
		},
		focusOut() {
			EventBus.$emit('focusout');
		},
	},
};
</script>

<style>
/* ::-webkit-scrollbar {
	width: 0px;
	height: 6px;
} */
</style>
