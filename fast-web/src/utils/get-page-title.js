const title = 'Fast Frame'

/**
 * 获取页面标题
 * @param {string} pageTitle 页面标题
 * @returns {string}
 */
export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return title
}