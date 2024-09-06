package team.seventhmile.tripforp.domain.magazine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.entity.QMagazine;

@RequiredArgsConstructor
@Repository
public class MagazineRepositoryImpl implements MagazineRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMagazine qMagazine = QMagazine.magazine;

	/**
	 * 모든 매거진 게시글을 페이징하여 조회합니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 페이징된 매거진 게시글 리스트
	 */
	@Override
	public Page<Magazine> getMagazinePosts(Pageable pageable) {
		// 매거진 게시글을 페이징 기준으로 조회
		List<Magazine> magazines = queryFactory.selectFrom(qMagazine)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		// 전체 매거진 게시글 수를 계산하는 쿼리
		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine);

		// 페이징된 결과를 반환
		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}

	/**
	 * 키워드를 사용하여 매거진 게시글을 검색합니다 (제목 및 내용 기준).
	 *
	 * @param keyword 검색할 키워드
	 * @param pageable 페이징 정보
	 * @return 키워드로 검색된 페이징된 매거진 게시글 리스트
	 */
	@Override
	public Page<Magazine> getMagazineKeywordContaining(String keyword, Pageable pageable) {
		// 제목이나 내용에 키워드가 포함된 조건 생성
		BooleanExpression searchKeyword = qMagazine.title.containsIgnoreCase(keyword)
			.or(qMagazine.content.containsIgnoreCase(keyword));

		// 키워드를 기준으로 매거진 게시글 조회
		List<Magazine> magazines = queryFactory.selectFrom(qMagazine)
			.where(searchKeyword)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		// 키워드를 기준으로 전체 매거진 게시글 수를 계산하는 쿼리
		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine)
			.where(searchKeyword);

		// 페이징된 결과를 반환
		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}
}
