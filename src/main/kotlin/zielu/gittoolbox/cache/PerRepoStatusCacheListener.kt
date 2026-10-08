package zielu.gittoolbox.cache

import git4idea.repo.GitRepository

internal interface PerRepoStatusCacheListener {
  fun stateChanged(previous: RepoInfo, current: RepoInfo, repository: GitRepository) {
    stateChanged(current, repository)
  }

  fun stateChanged(info: RepoInfo, repository: GitRepository) {}

  fun evicted(repositories: Collection<GitRepository>) {}

  fun allRepositoriesInitialized(repositories: Collection<GitRepository>) {}
}
