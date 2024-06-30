/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import {registerPlugins} from '@/plugins'
import bus from '@/plugins/bus'
import _ from 'lodash';

// Components
import App from './App.vue'

// Composables
import {createApp} from 'vue'

const app = createApp(App)
app.config.globalProperties.$bus = bus
app.config.globalProperties.$_ = _;

registerPlugins(app)

app.mount('#app')
