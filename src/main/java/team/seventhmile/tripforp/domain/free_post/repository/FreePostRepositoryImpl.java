package team.seventhmile.tripforp.domain.free_post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.entity.QFreePost;

import java.util.List;

/**
 * FreePostRepositoryCustom 인터페이스의 구현체입니다. QueryDSL을 사용하여 복잡한 쿼리를 수행합니다.
 */
@RequiredArgsConstructor
@Repository
public class FreePostRepositoryImpl implements FreePostRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QFreePost qFreePost = QFreePost.freePost;

	/**
	 * 페이징된 FreePost 목록을 가져옵니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 페이징된 FreePost 페이지 객체
	 */
	@Override
	public Page<FreePost> getFreePosts(Pageable pageable) {
		// FreePost 엔티티를 기준으로 페이징된 목록을 조회합니다.
		List<FreePost> freePosts = queryFactory.selectFrom(qFreePost)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		// 전체 레코드 수를 카운트합니다.
		JPAQuery<Long> count = queryFactory
			.select(qFreePost.count())
			.from(qFreePost);

		// 페이징된 결과를 반환합니다.
		return PageableExecutionUtils.getPage(freePosts, pageable, count::fetchOne);

	}

	/**
	 * 주어진 키워드를 포함하는 FreePost를 검색합니다.
	 *
	 * @param keyword  검색할 키워드
	 * @param pageable 페이징 정보
	 * @return 페이징된 검색 결과 FreePost 페이지 객체
	 */
	@Override
	public Page<FreePost> getFreePostKeywordContaining(String keyword, Pageable pageable) {
		// 제목이나 내용에 키워드가 포함된 레코드를 조회합니다.
		BooleanExpression searchKeyword = qFreePost.content.containsIgnoreCase(keyword);

		List<FreePost> freePosts = queryFactory.selectFrom(qFreePost)
			.where(searchKeyword)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		// 검색 결과에 대한 전체 레코드 수를 카운트합니다.
		JPAQuery<Long> count = queryFactory
			.select(qFreePost.count())
			.from(qFreePost)
			.where(searchKeyword);

		// 페이징된 검색 결과를 반환합니다.
		return PageableExecutionUtils.getPage(freePosts, pageable, count::fetchOne);
	}
}
