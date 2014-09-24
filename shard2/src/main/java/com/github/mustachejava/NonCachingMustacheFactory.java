package com.github.mustachejava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class NonCachingMustacheFactory extends DefaultMustacheFactory {

	public NonCachingMustacheFactory(String resourceRoot) {
		super(resourceRoot);
	}

	@Override
	protected LoadingCache<String, Mustache> createMustacheCache() {
		return CacheBuilder.newBuilder().maximumSize(0)
				.build(new MustacheCacheLoader());
	}

}
